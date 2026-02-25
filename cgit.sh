git add --all
git commit -m "${1}"
branch=`git branch --show-current`
git push -u origin ${branch}

