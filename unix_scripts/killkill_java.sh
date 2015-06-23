kill -KILL `ps -ef | fgrep java | fgrep -v fgrep | awk '{print $2}'`
rm target/universal/stage/RUNNING_PID
