@echo off

echo "
echo "    _____  _                                           
echo "   /  __ \| |                       
echo "   | /  \/| |__    __ _   ___   ___ 
echo "   | |    | '_ \  / _` | / _ \ / __|
echo "   | \__/\| | | || (_| || (_) |\__ \
echo "    \____/|_| |_| \__,_| \___/ |___/
echo "                 _____        
echo "                |_   _|       
echo "                  | |    ___  
echo "                  | |   / _ \ 
echo "                  | |  | (_) |
echo "                  \_/   \___/ 
echo "            _____                                    
echo "           /  __ \                                   
echo "           | /  \/  ___   ___  _ __ ___    ___   ___ 
echo "           | |     / _ \ / __|| '_ ` _ \  / _ \ / __|
echo "           | \__/\| (_) |\__ \| | | | | || (_) |\__ \
echo "           \____/  \___/ |___/|_| |_| |_| \___/ |___/
echo "
echo "
echo "                                      Writen by 9ins.
echo "
echo "


SET CLASSPATH=./tcp-proxy-1.0.0.jar

java -classpath %CLASSPATH% org.chaostocosmos.net.tcp.TCPProxy config.yml


pause

