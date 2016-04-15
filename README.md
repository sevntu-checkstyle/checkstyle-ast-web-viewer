# checkstyle-ast-web-viewer
Vaadin webapp for displaying Checkstyle Synthax tree (AST) visually in browser


### How to build: 
- Install Docker 1.9+
- Use https://github.com/sevntu-checkstyle/checkstyle-ast-web-viewer/blob/master/docker/build-docker.sh

You will get a deployable Docker image which will be ready to run locally with command below:
```
docker run --rm --net host checkstyle/ast-web-viewer
```

How to deploy:
- Deploy updated Docker image 'checkstyle/ast-web-viewer' to Docker Hub (howto: https://docs.docker.com/docker-hub/repos/)
- Then login to 128.199.42.52, and deploy this image there:

```
docker run -d --rm --net host checkstyle/ast-web-viewer
```

For now, there is no DNS name and no SSL for this host. But we will have them soon )
