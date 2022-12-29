In the dockerfile, it uses a docker image which is stolen from nightscape/scala-mill, but i needed it built for a different architecutre and so rolled my own via github actions.

The predef.sc scripts can be used to configure private (corporate) repositories, if you need that, and you're running in some sort of CI environment. 

in theory, backend.prepareOffline oguht to cache the dependancies, taking advantage of dockers cachcing mechanisms... I'm not sure how well it works in practise... 

Particulaly in combination with NPM... currently missing in the dockerfile, is the build of the frontend... 