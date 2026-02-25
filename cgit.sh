git add --all
git commit -m "${1}"
branch=`git branch --show-current`
echo git push -u origin ${branch}

