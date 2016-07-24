# AndroidLintTools

This project aims to provide tools for Android resources clean (for example, clean unused resources).

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
$ arcleaner demo/build/outputs/lint-results-debug.xml 
Clean unused resources according to demo/build/outputs/lint-results-debug.xml...
ARCleaner args: [demo/build/outputs/lint-results-debug.xml]
$ git st
On branch master
Your branch is up-to-date with 'origin/master'.
Changes not staged for commit:
  (use "git add/rm <file>..." to update what will be committed)
  (use "git checkout -- <file>..." to discard changes in working directory)

	deleted:    demo/src/main/res/drawable-v21/ic_menu_camera.xml
	deleted:    demo/src/main/res/drawable-v21/ic_menu_gallery.xml
	deleted:    demo/src/main/res/drawable-v21/ic_menu_manage.xml
	deleted:    demo/src/main/res/drawable-v21/ic_menu_send.xml
	deleted:    demo/src/main/res/drawable-v21/ic_menu_share.xml
	deleted:    demo/src/main/res/drawable-v21/ic_menu_slideshow.xml
	deleted:    demo/src/main/res/drawable/side_nav_bar.xml
	deleted:    demo/src/main/res/layout/activity_main.xml
	deleted:    demo/src/main/res/layout/app_bar_main.xml
	deleted:    demo/src/main/res/layout/nav_header_main.xml
	deleted:    demo/src/main/res/menu/activity_main_drawer.xml
	deleted:    demo/src/main/res/menu/main.xml
	modified:   demo/src/main/res/values/dimens.xml
	modified:   demo/src/main/res/values/drawables.xml
	deleted:    demo/src/main/res/values/navigation_strings.xml
	modified:   demo/src/main/res/values/strings.xml

no changes added to commit (use "git add" and/or "git commit -a")
```

### Only clean unused files

Show the usage of 'arcleaner':

```
$ arcleaner 
Usage: arcleaner <lint result XML file> [<true|false> <fileMatchReg>]
```

The second parameter (optional) for 'arcleaner' indicates if only clean unused files 
(not including the resources items in 'values' files).

For example,

```
$ ./gradlew :demo:lint
...

$ arcleaner demo/build/outputs/lint-results-debug.xml true
Clean unused resources according to demo/build/outputs/lint-results-debug.xml...
ARCleaner args: [demo/build/outputs/lint-results-debug.xml, true]

$ git st
On branch master
Your branch is up-to-date with 'origin/master'.
Changes not staged for commit:
  (use "git add/rm <file>..." to update what will be committed)
  (use "git checkout -- <file>..." to discard changes in working directory)

	deleted:    demo/src/main/res/drawable/side_nav_bar.xml
	deleted:    demo/src/main/res/layout/activity_main.xml
	deleted:    demo/src/main/res/layout/app_bar_main.xml
	deleted:    demo/src/main/res/layout/nav_header_main.xml
	deleted:    demo/src/main/res/menu/activity_main_drawer.xml
	deleted:    demo/src/main/res/menu/main.xml

no changes added to commit (use "git add" and/or "git commit -a")
```

### Only clean some unused files

For exmpale,

```
$ arcleaner demo/build/outputs/lint-results-debug.xml false ".*/res/layout/.*"
Clean unused resources according to demo/build/outputs/lint-results-debug.xml...
ARCleaner args: [demo/build/outputs/lint-results-debug.xml, false, .*/res/layout/.*]

$ git st
On branch master
Your branch is up-to-date with 'origin/master'.
Changes not staged for commit:
  (use "git add/rm <file>..." to update what will be committed)
  (use "git checkout -- <file>..." to discard changes in working directory)

	deleted:    demo/src/main/res/layout/activity_main.xml
	deleted:    demo/src/main/res/layout/app_bar_main.xml
	deleted:    demo/src/main/res/layout/nav_header_main.xml

no changes added to commit (use "git add" and/or "git commit -a")
```
