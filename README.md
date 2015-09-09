# senser

Demo Project opencv + Flink streaming

For some Fun

* edu.tuberlin.senser.images.facedetection.video.WatchTV

For twitter, set `twitter.enabled=true`

provide credentials in

`src/main/resources/twittersource.properties`

    twitter.consumerKey=
    twitter.consumerSecret=
    twitter.token=
    twitter.secret=

**:warning: !! Don't check this file in !! :warning:**

select keywords in

`src/main/resources/application.propertie`


first build

    mvn compile -P generate-frontend

start

    mvn spring-boot:run

  starts `edu.tuberlin.senser.images.MainApp`

Visit <http://localhost:8080/>
