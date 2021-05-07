#!/bin/bash

patch_cli_jar="/Users/shiguotao/Desktop/脚本/tinker/tinker-patch-cli-1.9.14.14.jar"
patch_outpath="/Users/shiguotao/Desktop/脚本/tinker/output/"
config_path="/Users/shiguotao/Desktop/脚本/tinker/tinker_config.xml"

oldApkPath=$1
newApkPath=$2


if [ -z "$oldApkPath" ]
then
	echo "基准包不能为空"
	exit
elif !(expr "${oldApkPath}" : '.*\.apk$')
#. 表示任意字符
#.* 表示任意多个字符
#apk$ 表示以apk结尾
then
	echo "基准包格式不正确"
	exit
fi

if [ -z "$newApkPath" ]
then 
	echo "新安装包不能为空" 
	exit
elif !(expr "${newApkPath}" : '.*\.apk$')
then
	echo "新安装包格式不正确"
	exit
fi


java -jar "${patch_cli_jar}" -old "${oldApkPath}" -new "${newApkPath}" -config "${config_path}" -out "${patch_outpath}"