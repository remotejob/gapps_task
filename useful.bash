

#documentation

mvn clean site javadoc:javadoc 
cp -a target/site/. /home/juno/git/gapp_task_doc/site/

in doc dir
git add -A
git commit -a -m "docs update"
git push origin gh-pages

