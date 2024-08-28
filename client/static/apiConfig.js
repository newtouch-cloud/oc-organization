/**
 * @Description 环境变量配置
 * @param env 环境变量
 * @return 环境配置对象
 */
const conFun = (env) => {
  if (env === 'development') {
    return {
      NODE_ENV: "development",
      BASE_API: "/api",
      WEB_SOCKET_URL: "ws://127.0.0.1:9999/websocket/",
      REPORT_CLIENT_URL: "http://localhost:9528/",
      REPORT_SERVER_URL: "http://localhost:8081/",
      FILE_PREVIEW_URL: "http://377328t1x9.zicp.vip:52265/",
      KK_FILE_URL: "http://192.168.0.4:8012/onlinePreview?url="
    }
  } else if (env === 'test') {
    return {
      NODE_ENV: "test",
      BASE_API: "/organisation-back-test",
      WEB_SOCKET_URL: "/organisation-back-test/websocket/",
      REPORT_CLIENT_URL: "",
      REPORT_SERVER_URL: "",
      FILE_PREVIEW_URL: "",
      KK_FILE_URL: ""
    }
  } else {
    return {
      NODE_ENV: "production",
      BASE_API: "",
      WEB_SOCKET_URL: "",
      REPORT_CLIENT_URL: "",
      REPORT_SERVER_URL: "",
      FILE_PREVIEW_URL: "",
      KK_FILE_URL: ""
    }
  }
}
window.config = conFun
