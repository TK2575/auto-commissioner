docker create --name "tk-selenium" \
  -p 4445:4444 -p 5901:5900 \
  --memory 4096mb --shm-size 4g \
  --restart unless-stopped \
  selenium/standalone-firefox-debug:latest