# checkstyle-ast-web-viewer
Vaadin webapp for displaying Checkstyle Synthax tree (AST) visually in browser


### How to build: 
- Install Docker 1.9+, Java 8 and Maven 3.
- Run [build-docker.sh](https://github.com/sevntu-checkstyle/checkstyle-ast-web-viewer/blob/master/docker/build-docker.sh)

This script builds a Docker image with compiled app binaries which will be ready to run locally with command below:
```
docker run --rm --net host checkstyle/ast-web-viewer
```

### How to deploy:
- Deploy updated Docker image 'checkstyle/ast-web-viewer' to Docker Hub ([howto](https://docs.docker.com/docker-hub/repos), [page at docker.io](https://hub.docker.com/r/daniilyar/checkstyle-ast-web-viewer/)). Example:
```
sudo docker login 
sudo docker tag checkstyle/ast-web-viewer daniilyar/checkstyle-ast-web-viewer:<version>
sudo docker push daniilyar/checkstyle-ast-web-viewer:<version>
```

- Login to 128.199.42.52, and deploy the image from Docker Hub there with command below (TODO: automate via ansible + sh launcher):

```
docker run -d --restart always --net host checkstyle/ast-web-viewer:<version>
```

For now, there is no DNS name and no SSL for this host. But we will have them soon )
