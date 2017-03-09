/**
 * Created by guillaumemartinez on 19/10/2016.
 */
/**
 * Default API to GET and POST the server
 * @type {{post: API.post, get: API.get}}
 */
var API = {
  post: function (url, param, onSuccess) {
    $.ajax({
      type: 'post',
      url: `api/${url}`,
      data: param,
      dataType: 'json',
      statusCode: {
        200: function (data) {
          if (data.status === 'error') {
            $.notify.error({
              title: `Erreur POST ${url}`,
              text: data.message,
              duration: 10
            });
            return
          }
          onSuccess()
        }
      }
    }).fail(function (err) {
      $.notify.error({
        title: `Erreur POST ${url}`,
        text: err.statusText,
        duration: 10
      })
    })
  },
  get: function (url, param, onSuccess) {
    $.ajax({
      type: 'get',
      url: `api/${url}`,
      data: param,
      dataType: 'json',
      statusCode: {
        200: function (data) {
          if (data.status === 'error') {
            $.notify.error({
              title: `Erreur GET ${url}`,
              text: data.message,
              duration: 10
            });
            return
          }
          onSuccess(data.contenu)
        }
      }
    }).fail(function (err) {
      $.notify.error({
        title: `Erreur GET ${url}`,
        text: err.statusText,
        duration: 10
      })
    })
  }
};
