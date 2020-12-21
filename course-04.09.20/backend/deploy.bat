 ::Deploy to helios and forward port to communicate
 scp -P 2222 ./build/libs/backend-0.9.9.jar s265570@se.ifmo.ru:~/
 ssh -p 2222 s265570@se.ifmo.ru -L 9090:localhost:9090