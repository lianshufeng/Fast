git checkout --orphan latest_branch
git add -A
git commit -am "��ʼ���ֿ�"
git branch -D master
git branch -m master
git push -f origin master