from picamera.array import PiRGBArray
from picamera import PiCamera
import cv2
import numpy as np
import face_recognition
import pickle
import time as time
from time import localtime


import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from firebase_admin import storage

#auto message
import smtplib
from email.mime.text import MIMEText

# haarcascades face 인식 방식
face_cascade_name = '/home/pi/Desktop/haarcascades/haarcascade_frontalface_alt.xml'
face_cascade = cv2.CascadeClassifier()
#-- 1. Load the cascades
if not face_cascade.load(cv2.samples.findFile(face_cascade_name)):
    print('--(!)Error loading face cascade')
    exit(0)

# 특징데이터
encoding_file = '/home/pi/Desktop/encodings.pickle'
# load the known faces and embeddings
data = pickle.loads(open(encoding_file, "rb").read())


#파이어베이스 설정
# Fetch the service account key JSON file contents
cred = credentials.Certificate('/home/pi/Desktop/smarthome-af812-firebase-adminsdk-a7twe-6ddb9587ec.json')
# Initialize the app with a service account, granting admin privileges
firebase_admin.initialize_app(cred, {
    'databaseURL': 'https://smarthome-af812-default-rtdb.firebaseio.com/',
    'storageBucket': 'smarthome-af812.appspot.com' # gs:// 포함 하면 안됨
})
bucket = storage.bucket()
unknown_name = 'INTRUDER'
recognized_name = None # 직전 사람을 저장하기 위해 같은 사람 x
frame_count = 0
frame_interval = 8

frame_width = 640
frame_height = 480
frame_resolution = [frame_width, frame_height]
frame_rate = 16

#파이카메라 설정
# initialize the camera and grab a reference to the raw camera capture
camera = PiCamera()
camera.resolution = frame_resolution
camera.framerate = frame_rate
rawCapture = PiRGBArray(camera, size=(frame_resolution))
# allow the camera to warmup
time.sleep(0.1)

# 캡처 파일 
catured_image = '/home/pi/Desktop/captured_image.jpg'
#catured = open(catured_image,'wb')


# 세션 생성
s = smtplib.SMTP('smtp.gmail.com', 587)
# TLS 보안 시작
s.starttls()
# 로그인 인증
#ofncdrxszvfjuxwh
s.login('guen599777@gmail.com', 'vrkcprhxuvqdfprm')


#cctv 가동
# capture frames from the camera
for frame in camera.capture_continuous(rawCapture, format="bgr", use_video_port=True):
    start_time = time.time() # 시작시간
    # grab the raw NumPy array representing the image
    image = frame.array
    # 생성해 두었던 캡처 파일에 이미지 데이터 저장 
    camera.capture(catured_image)


    # transform gray image # 노이즈 제거
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
    #-- 얼굴 탐지
    faces = face_cascade.detectMultiScale(gray)

    #(top, right, bottom, left)
    rois = [(y, x + w, y + h, x) for (x, y, w, h) in faces]

    # roi 영역 -> rgb 형식으로 변환
    encodings = face_recognition.face_encodings(rgb, rois)

    names = []

    for encoding in encodings:
        # encodings, 피클 얼굴 특징 데이터와 현재 encoding 비교
        matches = face_recognition.compare_faces(data["encodings"],
            encoding)
        name = unknown_name

        # 얼굴이 매칭되면
        if True in matches:
            matchedIdxs = [i for (i, b) in enumerate(matches) if b]
            counts = {}

            for i in matchedIdxs:
                name = data["names"][i]
                counts[name] = counts.get(name, 0) + 1

            
            name = max(counts, key=counts.get)
        
        # update the list of names
        names.append(name)

    # loop over the recognized faces
    for ((top, right, bottom, left), name) in zip(rois, names):
        # draw the predicted face name on the image
        y = top - 15 if top - 15 > 15 else top + 15
        color = (0, 255, 0)
        line = 2
        #모르는 사람이라면
        if(name == unknown_name):

            color = (0, 0, 255)
            line = 1
            # 보낼 메시지 설정

            ref_message = db.reference('rtMessage')
            message_state = ref_message.child('state')
            mes = message_state.get()
    
            if mes == 'on':

                print("Send Message")
                msg = MIMEText('Hello House owner.\nAn intruder has been detected in the house. please check the VisitorList')
                msg['Subject'] = 'intruder detection'
                msg['From'] = 'guen599777@gmail.com'
                msg['To'] = 'guen5997@gmail.com'
                # 메일 보내기
                s.sendmail("guen599777@gmail.com", "guen5997@gmail.com", msg.as_string())
                # 세션 종료
                #s.quit()
            
          
            # 아는 사람
        if(name != recognized_name):
            recognized_name = name
            # Send Notice
            print("Send Notice")
            current = str(time.time())

            cur = localtime(time.time())
            cur_y = cur.tm_year
            cur_mon = cur.tm_mon
            cur_d = cur.tm_mday
            cur_h = cur.tm_hour
            cur_m = cur.tm_min
            cur_s = cur.tm_sec
            k_time = str(cur_y) + "-" + str(cur_mon) + "-"+ str(cur_d) +"/" + str(cur_h) +"h "+ str(cur_m) +"m "+ str(cur_s) + "s "
            
            
            blob = bucket.blob(current) # upload image name
            blob.upload_from_filename(catured_image) # fireStore 업로드
            #blob.make_public()
            
            visitor_key = str(time.time()).split('.')[0]
            # 실시간 db에 저장            
            ref = db.reference('visitor')
            box_ref = ref.child(visitor_key)

            box_ref.update({
                'name': name,
                'date': k_time, 
                'image': 'https://firebasestorage.googleapis.com/v0/b/smarthome-af812.appspot.com/o/'+current+'?alt=media,',
                'idx': visitor_key
            })
            



        cv2.rectangle(image, (left, top), (right, bottom), color, line)
        y = top - 15 if top - 15 > 15 else top + 15
        cv2.putText(image, name, (left, y), cv2.FONT_HERSHEY_SIMPLEX,
            0.75, color, line)
                
    end_time = time.time()
    process_time = end_time - start_time
    # 이미지 출력
    cv2.imshow("Recognition", image)

    key = cv2.waitKey(1) & 0xFF
    # clear the stream in preparation for the next frame
    rawCapture.truncate(0)
    # if the `q` key was pressed, break from the loop
    if key == ord("q"):
        s.quit()
        break
