# -*- coding: gbk -*-  

import os
import platform;
import subprocess
import signal
import sys


_mainClass = "per.chzopen.zkViewer.AppGUI";
_pidFile = "zkViewer.pid"


def isWindows():
    return platform.system().lower().find("windows")>-1


def isProcessExists(pid):
    _str = "";
    if isWindows():
        _str = os.popen('tasklist /FI "PID eq %s" | findstr "%s"' % (pid, pid)).read();
    else:
        _str = os.popen('ps -p "%s" | grep -i "%s"' % (pid, pid)).read();
    return _str.strip()!="";
    
    
def doKill(pid):
    try:
        print("kill: %s" % pid)
        if isProcessExists(pid):
            os.kill(int(pid), signal.SIGINT)
    except:
        pass


def readPidFile():
    try:
        _f = open(_pidFile, 'rb')
        _content = _f.read()
        _f.close()
        _dict = eval(_content)
        return _dict;
    except:
        return {};

    
def checkRunning():
    _dict = readPidFile();
    
    _b1 = isProcessExists(_dict.get("main_pid"));
    if _b1:
        print("main process %s is running" % _dict.get("main_pid"))
    
    _b2 = isProcessExists(_dict.get("sub_pid"));
    if _b2:
        print("sub process %s is running" % _dict.get("sub_pid"))
    
    if _b1 or _b2:
        return True;
    else:
        return False;
    


def killFromPid():
    try:
        _dict = readPidFile()
        doKill(_dict.get("main_pid"));
        doKill(_dict.get("sub_pid"));
    except:
        pass


def writePid(pid, spid):
    _dict = str({"main_pid":pid, "sub_pid":spid})
    print(_dict)
    _f = open(_pidFile, 'wb')
    _f.write(_dict)
    _f.close( )


def makeCmd():
    
    _sep = ":";
    
    if isWindows()>-1:
        _sep = ";"
    
    _cp = "target/classes" + _sep;
    
    for _pa_dir, _p2, filenames in os.walk("target/dependency"):
        for _filename in filenames:
            if _filename.lower().endswith(".jar"):
                _cp += os.path.join(_pa_dir, _filename) + _sep;
    
    _cmd = 'java -cp "%s" %s' % (_cp, _mainClass)
    return _cmd

        
def doStartup():
    _process = subprocess.Popen(makeCmd())
    writePid(os.getpid(), _process.pid)
    

if __name__=="__main__":
    
    _arg = "start";
    if len(sys.argv)>1:
        if sys.argv[1]=="restart":
            _arg = "restart";
        elif sys.argv[1]=="stop":
            _arg = "stop";
            
    if _arg=="start":
        
        if checkRunning()==False:
            doStartup();
        else:
            print("service is running, use 'restart' instead")
            
    elif _arg=="stop":
        
        killFromPid();
        
    elif _arg=="restart":
        
        killFromPid();
        doStartup();
        
    else:
        
        print("wrong argument")
    
    
        
    
    

















