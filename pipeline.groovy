stage("Pipeline") {
    def modules = """
    [
    {
        "ntag": "采集端前端代码打包",
        "localPath": "./",
        "name": "Watcher-web.tar.gz",
        "parameters": {
            "BUILD_VERSION": "${BUILD_VERSION}",
            "BUILD_BRANCH":"${BUILD_BRANCH}"
        },
        "path": "vdibigpackage/oad-watcher",
        "pipelineId": "1526776401523376128",
        "triggerCodeRepo": "http://djdam.h3c.com:21100/h3ccloud/cloud-om/oad-watcher.git",
        "version": "${BUILD_VERSION}"
    },
    {
        "ntag": "采集端后台包",
        "localPath": "./",
        "name": "Watcher-backend.tar.gz",
        "parameters": {
            "BUILD_VERSION": "${BUILD_VERSION}",
            "BUILD_BRANCH":"${BUILD_BRANCH}"
        },
        "path": "vdibigpackage/oad-watcher",
        "pipelineId": "1526776313321357312",
        "triggerCodeRepo": "http://djdam.h3c.com:21100/h3ccloud/cloud-om/oad-watcher.git",
        "version": "${BUILD_VERSION}"
    }
    ]
    """
    pullArtifactBatchWithGitTrigger modules: modules,  timeout: 90
}

stage("shell"){
    sh """
    ls -R
    tar xvf Watcher-backend.tar.gz
    tar xvf Watcher-web.tar.gz
    mv -f dist/*  watcher-backend/components/nginx/nginx/html

    echo "$OUT_VERSION $IN_VERSION" > watcher-backend/bin/watcher.version
    echo "$BUILD_VERSION" >> watcher-backend/bin/watcher.version
    echo "Build" `date` >> watcher-backend/bin/watcher.version
    echo "BRANCH:$BUILD_BRANCH" >> watcher-backend/bin/watcher.version

    mv watcher-backend oad-watcher
    tar -cvf OAD-Watcher-${OUT_VERSION}.tar.gz oad-watcher
    md5sum OAD-Watcher-${OUT_VERSION}.tar.gz > OAD-Watcher-${OUT_VERSION}.md5
    
    cp -R oad-watcher oad-watcher-$OUT_VERSION-upgrader
    cp -R oad-watcher-$OUT_VERSION-upgrader/components/nginx/nginx/html oad-watcher-$OUT_VERSION-upgrader
    cp -R oad-watcher-$OUT_VERSION-upgrader/components/inspect oad-watcher-$OUT_VERSION-upgrader
    rm -rf oad-watcher-$OUT_VERSION-upgrader/components
    rm -rf oad-watcher-$OUT_VERSION-upgrader/logs
    rm -rf oad-watcher-$OUT_VERSION-upgrader/temp
    rm -rf oad-watcher-$OUT_VERSION-upgrader/data
    rm -f oad-watcher-$OUT_VERSION-upgrader/bin/register_as_system_service_and_start.sh
    rm -f oad-watcher-$OUT_VERSION-upgrader/bin/register_keepalived_as_system_service.sh
    rm -f oad-watcher-$OUT_VERSION-upgrader/bin/restart.sh
    rm -f oad-watcher-$OUT_VERSION-upgrader/bin/shutdown.sh
    rm -f oad-watcher-$OUT_VERSION-upgrader/bin/status.sh
    rm -f oad-watcher-$OUT_VERSION-upgrader/bin/init.sh
    rm -f oad-watcher-$OUT_VERSION-upgrader/bin/init_slave.sh
    tar -cvf OAD-Watcher-${OUT_VERSION}-upgrader.tar.gz oad-watcher-$OUT_VERSION-upgrader
    md5sum OAD-Watcher-${OUT_VERSION}-upgrader.tar.gz > OAD-Watcher-${OUT_VERSION}-upgrader.md5

    pwd
    ls
    """
}
