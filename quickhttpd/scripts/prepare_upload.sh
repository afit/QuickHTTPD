#!/bin/bash

export CVS_RSH=ssh
export CVSROOT=:ext:$USER@localhost:/opt/cvs
export PATH=$PATH:/usr/ant/bin
export JAVA_HOME=/usr/java
export ANT_HOME=/usr/ant

FOLDER=QuickHTTPD-CVS-Nightly-`date +%d%m%Y`
rm -rf $FOLDER
mkdir $FOLDER
cd $FOLDER

cvs -Q co common_utils
cvs -Q export -r HEAD quickhttpd

cvs -Q co project_pool/lib/3rdparty/junit.jar
cvs -Q co project_pool/lib/3rdparty/xerces.jar
cvs -Q co project_pool/lib/common_utils.jar

cd "$OLDPWD"

ant -q -f $FOLDER/quickhttpd/scripts/build.xml doc > /dev/null
mv $FOLDER/quickhttpd/docs $FOLDER-api
mv $FOLDER/quickhttpd/index.html /var/www/html/live/quickhttpd
tar -cvjf $FOLDER.tar.bz2 $FOLDER > /dev/null

rm -rf $FOLDER
rm -rf /var/www/html/live/quickhttpd/$FOLDER-api

mv $FOLDER.tar.bz2 /var/www/html/live/quickhttpd
mv $FOLDER-api /var/www/html/live/quickhttpd
