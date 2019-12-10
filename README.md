# AndroidLintTools

This project aims to provide tools for Android resources clean (for example, clean unused resources).

> **TIPS**: Maybe you want to try the "Refactor > Remove Unused Resources..." feature in Android Studio.
Or you can try the "shrinkResources" option (refer https://developer.android.com/studio/build/shrink-code#shrink-resources).

## Install the tools

For example, install the tools to "~/bin" (if it's in your machine's PATH).

```
$ cd ~/bin
$ git clone https://github.com/yongce/AndroidLintTools
$ ln -s AndroidLintTools/cmd/arcleaner arcleaner
$ ll arcleaner
lrwxr-xr-x   1 yctu  staff     30  7 24 18:37 arcleaner@ -> AndroidLintTools/cmd/arcleaner
```

## Use 'arcleaner' to clean unused resources

First, get the lint result:

```
$ ./gradlew :demo:lint
```

Then, clean the unused resources (clean the 'demo' module):

```
$ arcleaner demo/build/reports/lint-results.xml
Clean unused resources according to demo/build/reports/lint-results.xml...
ARCleaner version: 1.1.0
args: [arcleaner, demo/build/reports/lint-results.xml]
working...
done

$ git st
位于分支 master
您的分支领先 'origin/master' 共 1 个提交。
  （使用 "git push" 来发布您的本地提交）

尚未暂存以备提交的变更：
  （使用 "git add/rm <文件>..." 更新要提交的内容）
  （使用 "git checkout -- <文件>..." 丢弃工作区的改动）

	删除：     demo/src/main/res/drawable-v21/ic_menu_camera.xml
	删除：     demo/src/main/res/drawable-v21/ic_menu_gallery.xml
	删除：     demo/src/main/res/drawable-v21/ic_menu_manage.xml
	删除：     demo/src/main/res/drawable-v21/ic_menu_send.xml
	删除：     demo/src/main/res/drawable-v21/ic_menu_share.xml
	删除：     demo/src/main/res/drawable-v21/ic_menu_slideshow.xml
	删除：     demo/src/main/res/drawable/side_nav_bar.xml
	删除：     demo/src/main/res/layout/activity_main.xml
	删除：     demo/src/main/res/layout/nav_header_main.xml
	删除：     demo/src/main/res/menu/activity_main_drawer.xml
	删除：     demo/src/main/res/menu/main.xml
	删除：     demo/src/main/res/values-v21/styles.xml
	删除：     demo/src/main/res/values-zh-rCN/strings.xml
	修改：     demo/src/main/res/values/dimens.xml
	删除：     demo/src/main/res/values/drawables.xml
	修改：     demo/src/main/res/values/strings.xml
	修改：     demo/src/main/res/values/styles.xml

修改尚未加入提交（使用 "git add" 和/或 "git commit -a"）
```

### Only clean unused files

Show the usage of 'arcleaner':

```
$ arcleaner
ARCleaner version: 1.1.0
Usage: arcleaner <lint result XML file> [<true|false> <fileMatchReg>]
```

The second parameter (optional) for 'arcleaner' indicates if only clean unused files
(not including the resources items in 'values' files).

For example,

```
$ ./gradlew :demo:lint
...

$ arcleaner demo/build/reports/lint-results.xml true
Clean unused resources according to demo/build/reports/lint-results.xml...
ARCleaner version: 1.1.0
args: [arcleaner, demo/build/reports/lint-results.xml, true]
working...
done

$ git st
位于分支 master
您的分支领先 'origin/master' 共 1 个提交。
  （使用 "git push" 来发布您的本地提交）

尚未暂存以备提交的变更：
  （使用 "git add/rm <文件>..." 更新要提交的内容）
  （使用 "git checkout -- <文件>..." 丢弃工作区的改动）

	删除：     demo/src/main/res/drawable/side_nav_bar.xml
	删除：     demo/src/main/res/layout/activity_main.xml
	删除：     demo/src/main/res/layout/nav_header_main.xml
	删除：     demo/src/main/res/menu/activity_main_drawer.xml
	删除：     demo/src/main/res/menu/main.xml

修改尚未加入提交（使用 "git add" 和/或 "git commit -a"）
```

### Only clean some files

For exmpale,

```
$ arcleaner demo/build/reports/lint-results.xml false ".*/res/layout/.*"
Clean unused resources according to demo/build/reports/lint-results.xml...
ARCleaner version: 1.1.0
args: [arcleaner, demo/build/reports/lint-results.xml, false, .*/res/layout/.*]
working...
done

$ git st
位于分支 master
您的分支领先 'origin/master' 共 1 个提交。
  （使用 "git push" 来发布您的本地提交）

尚未暂存以备提交的变更：
  （使用 "git add/rm <文件>..." 更新要提交的内容）
  （使用 "git checkout -- <文件>..." 丢弃工作区的改动）

	删除：     demo/src/main/res/layout/activity_main.xml
	删除：     demo/src/main/res/layout/nav_header_main.xml

修改尚未加入提交（使用 "git add" 和/或 "git commit -a"）
```
