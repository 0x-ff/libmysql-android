# CMake generated Testfile for 
# Source directory: /media/pavel/projects/libmysql-android/libmysqlclient/unittest/mysys
# Build directory: /media/pavel/projects/libmysql-android/libmysqlclient/unittest/mysys
# 
# This file includes the relevent testing commands required for 
# testing this directory and lists subdirectories to be tested as well.
ADD_TEST(bitmap-t "/bitmap-t")
SET_TESTS_PROPERTIES(bitmap-t PROPERTIES  TIMEOUT "120")
ADD_TEST(base64-t "/base64-t")
SET_TESTS_PROPERTIES(base64-t PROPERTIES  TIMEOUT "120")
ADD_TEST(my_atomic-t "/my_atomic-t")
SET_TESTS_PROPERTIES(my_atomic-t PROPERTIES  TIMEOUT "120")
ADD_TEST(lf-t "/lf-t")
SET_TESTS_PROPERTIES(lf-t PROPERTIES  TIMEOUT "120")
ADD_TEST(waiting_threads-t "/waiting_threads-t")
SET_TESTS_PROPERTIES(waiting_threads-t PROPERTIES  TIMEOUT "120")
