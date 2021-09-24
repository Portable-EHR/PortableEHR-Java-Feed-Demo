#!/bin/bash

#
# Copyright © Portable EHR inc, 2021
# https://portableehr.com/
#

# Need help -> runFeed.sh -h

function showHelp {
  echo "#"
  echo "# Usage : runFeed.sh [-b ${BIN_FOLDER}] [-p ${SERVER_PORT}] [-m ${MOCKS_BASE_PATH}]"
  echo "#       [-f ${FEED_HUB_BASE_PATH}] [--sslFile ${SSL_KEY_STORE_FILE}] [--sslType ${SSL_KEY_STORE_TYPE}]"
  echo "#       [--sslPass ${SSL_KEY_STORE_PASSWORD}] [--sslAlias ${SSL_KEY_ALIAS}] [-h|--help]"
  echo "# Options :"
  echo "# -b BIN_FOLDER The folder where the portableehr-java-feed.jar is"
  echo "#     Default value : ./bin"
  echo "# -p SERVER_PORT The port the Feed will use"
  echo "#     Default value : 4004"
  echo "# -m MOCKS_BASE_PATH The path to the mocks folder"
  echo "#     Default value : ./"
  echo "# -f FEED_HUB_BASE_PATH The base path to FeedHub"
  echo "#     Default value : https://feed.portableehr.io:3004"
  echo "# --sslFile SSL_KEY_STORE_FILE The keystore containing the ssl key"
  echo "#     Default value : classpath:PortableEHRJavaFeed.p12"
  echo "# --sslType SSL_KEY_STORE_TYPE The type of keystore"
  echo "#     Default value : PKCS12"
  echo "# --sslPass SSL_KEY_STORE_PASSWORD The password of the keystore"
  echo "#     Default value : changeit"
  echo "# --sslAlias SSL_KEY_ALIAS The key alias in the keystore"
  echo "#     Default value : PEHRFeed"
  echo "# -h|--help Show this help"
  echo "#"
}

BIN_FOLDER='./bin'
SERVER_PORT=4004
MOCKS_BASE_PATH='./'
FEED_HUB_BASE_PATH='https://feed.portableehr.io:3004'
SSL_KEY_STORE_FILE='classpath:PortableEHRJavaFeed.p12'
SSL_KEY_STORE_TYPE='PKCS12'
SSL_KEY_STORE_PASSWORD='changeit'
SSL_KEY_ALIAS='PEHRFeed'

POSITIONAL=()
while [[ $# -gt 0 ]]; do
  key="$1"

  case $key in
    -b)
      BIN_FOLDER="$2"
      shift # past argument
      shift # past value
      ;;
    -p)
      SERVER_PORT="$2"
      shift # past argument
      shift # past value
      ;;
    -m)
      MOCKS_BASE_PATH="$2"
      shift # past argument
      shift # past value
      ;;
    -f)
      FEED_HUB_BASE_PATH="$2"
      shift # past argument
      shift # past value
      ;;
    --sslFile)
      SSL_KEY_STORE_FILE="$2"
      shift # past argument
      shift # past value
      ;;
    --sslType)
      SSL_KEY_STORE_TYPE="$2"
      shift # past argument
      shift # past value
      ;;
    --sslPass)
      SSL_KEY_STORE_PASSWORD="$2"
      shift # past argument
      shift # past value
      ;;
    --sslAlias)
      SSL_KEY_ALIAS="$2"
      shift # past argument
      shift # past value
      ;;
    -h|--help)
      showHelp
      exit 0
      ;;
    *)    # unknown option
      POSITIONAL+=("$1") # save it in an array for later
      shift # past argument
      ;;
  esac
done

if (( ${#POSITIONAL[@]} )); then
    echo "Unknown options : $POSITIONAL"
    echo "Try 'runFeed.sh -h'"
    exit 1;
fi

pid=`cat feed.pid`
echo "Killing old Java Feed (PID=$pid)"
kill $pid

java -jar "$BIN_FOLDER"/portableehr-java-feed.jar \
          --server.port="$SERVER_PORT" \
          --mocks.basePath="$MOCKS_BASE_PATH" \
          --feedhub.basePath="$FEED_HUB_BASE_PATH"  \
          --server.ssl.key-store="$SSL_KEY_STORE_FILE" \
          --server.ssl.key-store-type="$SSL_KEY_STORE_TYPE" \
          --server.ssl.key-store-password="$SSL_KEY_STORE_PASSWORD" \
          --server.ssl.key-alias="$SSL_KEY_ALIAS" \
          > portableehr-java-feed.log &
echo $! > feed.pid

echo "Tailing portableehr-java-feed.log..."
tail -F portableehr-java-feed.log