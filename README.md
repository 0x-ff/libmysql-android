libmysql-android
================

Native "C" libmysql porting for Android (arm, API Levels 8,9,14)

Как пересобрать libmysql ?

Вообще у NDK есть клевая документация как и cmake и у mysql, однако.

1. 
Сначала нужно подготовить среду для кросскомпилляции

```
	$NDK/build/tools/make-standalone-toolchain.sh \
		--arch=<arch> \
		--platform=<api level> \
		--install-dir=$YOUR_STANDALONE_TOOLCHAINS_ROOT/<api level>/<arch> --ndk-dir=$NDK
```
$NDK - путь к NDK
(у меня /media/pavel/android/ndk-crystax)
$YOUR_STANDALONE_TOOLCHAINS_ROOT - общий путь для кросскомпилляции
(у меня /media/pavel/android/standalone-toolchains)
	
Нужно выбрать 
<api level> API Level  
android-8 для Android2.2,
android-9 для Android2.3,
android-14 для Android4.0
и <arch> архитектуру (x86|arm)

	
2.
Сделать симлинк в каталоге 
	
```
		$YOUR_STANDALONE_TOOLCHAINS_ROOT/<api level>/<arch>/sysroot/usr/include/net
		ln -s if_ether.h ethernet.h
```	

3.
Изменяем генератор include/my_config.h.in добавляем в него следующие строчки

```
typedef unsigned short ushort;
#define S_IREAD 0400
#define HAVE_RINT 0
```
4.
Создаем Makefile

```
cmake 2.6.x:
    cmake -G "Unix Makefiles" -DCMAKE_INSTALL_PREFIX=`pwd`/install -DCMAKE_TOOLCHAIN_FILE=<api level><arch>.cmake.2.6

cmake 2.8.x:
    cmake -G "Unix Makefiles" -DCMAKE_INSTALL_PREFIX=`pwd`/install -DCMAKE_TOOLCHAIN_FILE=<api level><arch>.cmake
```
5.
Собираем:

```
make libmysql
make mysqlclient
```
6.
Смотрим в каталог libmysql/ 
там будет libmysql.so и mysqlclient.a

