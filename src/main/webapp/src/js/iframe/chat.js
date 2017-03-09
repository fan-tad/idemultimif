/**
 * Created by guillaumemartinez on 22/11/2016.
 */

$(function () {
  var MESSAGE_COUNT = 0;
  /**
   * Post message on press enter
   */
  $('body').on('keydown', function (event) {
    if (event.keyCode === 13 && $('body .chat .new-message').is(":focus")) {
      $('body .chat .send-message').trigger('click')
    }
  })
  /**
   * Open / Close chat
   */
  $('body .chat .close-chat').on('click', function () {
    if ($('body .chat').attr('id') == 'closed') {
      $('body .chat').attr('id', '')
    } else (
      $('body .chat').attr('id', 'closed')
    )
  })
  /**
   * Post message on press button
   */
  $('body .chat .send-message').on('click', function () {
    postMessage($('body .chat .new-message').val(), function () {
      addTempMessage('Me', $('body .chat .new-message').val())
      $('body .chat .new-message').val('')
    })
  })
  /**
   * Adding a temporary message. Will be removed after getMessage from the server
   * @param user
   * @param text
   */
  function addTempMessage (user, text) {
    $('body .chat .message-list').append(
      $('<li>').addClass('message toDelete').text(`${user}: ${text}`)
    )
  }

  /**
   * Add message to the page
   * @param user
   * @param text
   */
  function addMessage (user, text) {
    $('body .chat .message-list').append(
      $('<li>').addClass('message').text(`${user}: ${text}`)
    )
  }

  /*function clearMessage () {
   $('body .chat .message-list .message').remove()
   }*/
  /**
   * Get messages from the server
   * @param onSuccess
   */
  function getMessages (onSuccess) {
    API.get('/message', {
      project: QueryString.project,
      numero: MESSAGE_COUNT
    }, onSuccess)
  }

  /**
   * Envoyer un message au serveur
   * @param message
   * @param onSuccess
   */
  function postMessage (message, onSuccess) {
    if(message === "" || message == null){
      return
    }
    API.post('/message', {
      project: QueryString.project,
      message: message
    }, onSuccess)
  }

  /**
   * Get all messages every 5 seconds
   */
  setInterval(function () {
    getMessages(function (messages) {
      //clearMessage()
      $('body .chat .message-list .toDelete').remove()
      for (var m in messages) {
        MESSAGE_COUNT++;
        var message = messages[m]
        addMessage(message.user, message.message)
      }
      $('.message-list').animate({scrollTop: $('.message-list').get(0).scrollHeight}, 1);
    })
  }, 5000)
})