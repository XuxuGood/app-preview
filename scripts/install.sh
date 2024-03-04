#!/bin/bash

#配置JAVA_HOME
JAVA_PATH_DIR=$JAVA_HOME

if [ -z "$JAVA_PATH_DIR" ]; then
    echo "JAVA_HOME is empty"
    exit 0;
fi

if [ ! -d "$JAVA_PATH_DIR" ]; then
    echo "目录不存在"
    exit 0;
fi

# shellcheck disable=SC2154
if [[ $Env == Product ]];then
    echo "线上环境不操作此脚本"
    exit 0;
fi

# shellcheck disable=SC2164
DIR_BASE=$(cd "$(dirname $0)"; pwd)

mkdir -p "$JAVA_PATH_DIR"/jre/lib/amd64/dcevm/
# shellcheck disable=SC2181
if [[ $? -ne 0 ]]; then
    echo "创建目录失败"
    exit 0
fi

# 判断版本号是否大于等于181
java_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')

echo "Java版本:$java_version"
version_number=0
if [[ $java_version =~ ([0-9]+)$ ]]; then
    version_number=${BASH_REMATCH[1]}
fi
echo "version_number:$version_number"

if [[ $version_number -gt 181 || $version_number -eq 181 ]]; then
    echo "Java版本大于或等于181"
    cp "$DIR_BASE"/dcevm/libjvm181.so "$JAVA_PATH_DIR"/jre/lib/amd64/dcevm/libjvm.so
elif [[ $version_number -eq 172 ]]; then
		echo "Java版本:172"
		cp "$DIR_BASE"/dcevm/libjvm172.so "$JAVA_PATH_DIR"/jre/lib/amd64/dcevm/libjvm.so
elif [[ $version_number -eq 152 ]]; then
		echo "Java版本:152"
    cp "$DIR_BASE"/dcevm/libjvm152.so "$JAVA_PATH_DIR"/jre/lib/amd64/dcevm/libjvm.so
elif [[ $version_number -eq 144 ]]; then
		echo "Java版本:144"
    cp "$DIR_BASE"/dcevm/libjvm144.so "$JAVA_PATH_DIR"/jre/lib/amd64/dcevm/libjvm.so
elif [[ $version_number -eq 112 ]]; then
		echo "Java版本:112"
    cp "$DIR_BASE"/dcevm/libjvm112.so "$JAVA_PATH_DIR"/jre/lib/amd64/dcevm/libjvm.so
elif [[ $version_number -eq 92 ]]; then
		echo "Java版本:92"
    cp "$DIR_BASE"/dcevm/libjvm92.so "$JAVA_PATH_DIR"/jre/lib/amd64/dcevm/libjvm.so
elif [[ $version_number -eq 74 ]]; then
		echo "Java版本:74"
    cp "$DIR_BASE"/dcevm/libjvm74.so "$JAVA_PATH_DIR"/jre/lib/amd64/dcevm/libjvm.so
else
    echo "Java版本小于等于66"
    cp "$DIR_BASE"/dcevm/libjvm.so "$JAVA_PATH_DIR"/jre/lib/amd64/dcevm
fi

# shellcheck disable=SC2181
if [[ $? -ne 0 ]]; then
    echo "拷贝libjvm.so失败"
    exit 0
fi
echo "拷贝libjvm.so完成"