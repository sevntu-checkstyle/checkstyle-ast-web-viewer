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

- Deploy new app version to Checkstyle Scaleway instance (347c5f47-a226-4ed6-a4ff-a01680db9660.pub.cloud.scaleway.com or 212.47.237.40):

```
deployment/run.sh
```

DY: this script requires Ansible 1.9.4+ to be installed on your machine (`pip install ansible==1.9.4`)

- Test how it works by opening http://347c5f47-a226-4ed6-a4ff-a01680db9660.pub.cloud.scaleway.com

DY: For now, there is no pretty DNS name for this host. But we will have it soon )

