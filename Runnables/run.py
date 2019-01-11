import subprocess
import sys
import os

path = os.path.dirname(os.path.realpath(__file__)) + "\\"

java = subprocess.Popen(['java', '-jar', path + '\\MappingAgentSoftware.jar'], stdout=subprocess.PIPE)

print("Java running ...")

python = subprocess.Popen(['python',  path + '\\main.py'], stdout=subprocess.PIPE)

print("Python running ...")

unity = subprocess.Popen(path + 'multi-agent-system-mapping-ia54.exe',stdout=subprocess.PIPE)

print("Unity running ...")

while True:
    output = java.stdout.readline()
    if output == '' and java.poll() is not None:
        stop = input("Pour arrêter les programmes veuillez appuyer sur la touche o.")
        if stop == "o":
            python.kill()
            unity.kill()
            java.kill()       
        break 
    if output:
        print(output.strip())

stop = input("Pour arrêter les programmes veuillez appuyer sur la touche o.")
if stop == "o":
    python.kill()
    unity.kill()
    java.kill()