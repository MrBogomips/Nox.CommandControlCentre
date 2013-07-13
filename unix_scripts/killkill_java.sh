kill -KILL `ps -ef | fgrep java | fgrep -v fgrep | awk '{print $2}'`
