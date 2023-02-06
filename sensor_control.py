import Adafruit_DHT
import time

import RPi.GPIO as GPIO

import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from gpiozero import RGBLED

led1=RGBLED(red=14 ,green=15 ,blue=18)
led2=RGBLED(red=17 ,green=27 ,blue=22)
Hit=RGBLED(red=23 ,green=24 ,blue=25)
GPIO.setmode(GPIO.BCM)
GPIO.setup(20,GPIO.OUT)

sensor=Adafruit_DHT.DHT11

pin = 2

if not firebase_admin._apps:
    cred = credentials.Certificate('/home/pi/Desktop/smarthome-af812-firebase-adminsdk-a7twe-6ddb9587ec.json')
    firebase_admin.initialize_app(cred, {
        'databaseURL': 'https://smarthome-af812-default-rtdb.firebaseio.com/',
        'storageBucket': 'gs://smarthome-af812.appspot.com'
    })

try:
    while True:    
        humi,temp=Adafruit_DHT.read_retry(sensor,pin)
        unix=time.strftime('%Y-%m-%d %H:%M:%S')

        if humi is not None and temp is not None:
            print('Temp={0:0.1f}*C Humi={1:0.1f}%'.format(temp,humi),unix)
            
            ref = db.reference('data')
            ref.update({
                'Humiduty': humi,
                'Temperature' : temp,

                })
            snapshot = ref.order_by_key().get()
            print('succes')
        else:
            print('Fail')
            time.sleep(10)
        refled = db.reference('LED')
        led1s = refled.child('led1')
        led2s = refled.child('led2')
        
        refH = db.reference('hit')
        Hits = refH.child('power')
        setH = refH.child('setting')
        
        refM = db.reference('moter')
        moters = refM.child('power')
        setM = refM.child('setting')
        
        if(led1s.get()=='on'):
            led1.on()
        else:
            led1.off()
        if(led2s.get()=='on'):
            led2.on()
        else:
            led2.off()
            
        if(Hits.get()=='on'):
            Hit.red=1
        elif(setH.get()>=temp):
            print('hit=',setH.get())
            Hit.red=1
        else:
            Hit.red=0            
            
        if(moters.get()=='on'):
            GPIO.output(20,True)
#        elif(format(setM.get())<=format(temp)):
        elif(setM.get()<=temp):
            GPIO.output(20,True)
            print('moter=',setM.get())
        else:
            GPIO.output(20,False)        
            
except KeyboardInterrupt:
    print("Terminated by Keyboard")
finally:
    print("End of Program")