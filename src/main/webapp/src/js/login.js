/**
 * Created by guillaumemartinez on 14/10/2016.
 */

$(function () {
  /**
   * Switch beetwen login and register
   */
  $('.card .switch').on('click', switchCard);
  /**
   * Submit login
   */
  var currentSubmit = $('.card#login .submit').on('click', postLogin);
  /**
   * Submit register
   */
  $('.card#register .submit').on('click', postRegister);
  /**
   * Press enter to validate form
   */
  $('body').on('keydown', function (event) {
    if (event.keyCode === 13) {
      currentSubmit.trigger('click')
    }
  });
  /**
   * Switch bettwen login and register
   */
  function switchCard () {
    var $cLogin = $('.card#login');
    var $cRegister = $('.card#register');
    if ($cLogin.css('display') === 'none') {
      currentSubmit = $cLogin.find('.submit');
      $cLogin.show();
      $cRegister.hide();
      $('.card#login input#pseudo').val('');
      $('.card#login input#pass').val('')
    } else {
      currentSubmit = $cRegister.find('.submit');
      $cLogin.hide();
      $cRegister.show();
      $('.card#register input#pseudo').val('');
      $('.card#register input#pass').val('');
      $('.card#register input#pass2').val('')
    }
  }

  /**
   * Try to login
   */
  function postLogin () {
    var pseudo = $('.card#login input#pseudo').val();
    var password = $('.card#login input#pass').val();
    if (pseudo === '' || password === '') {
      $.notify.warning({
        title: 'Attention',
        text: 'Tous les champs doivent être rempli.'
      });
      return
    }
    API.post('/login', {
      pseudo: pseudo,
      password: password
    }, function () {
      $.notify.success({
        title: 'Success',
        text: 'Authentification'
      });
      window.location = 'projects.html'
    })
  }

  /**
   * Try to register
   */
  function postRegister () {
    var pseudo = $('.card#register input#pseudo').val();
    var password = $('.card#register input#pass').val();
    var password2 = $('.card#register input#pass2').val();
    if (pseudo === '' || password === '' || password2 === '') {
      $.notify.warning({
        title: 'Attention',
        text: 'Tous les champs doivent être rempli.'
      });
      return
    }
    if (password !== password2) {
      $.notify.warning({
        title: 'Attention',
        text: 'Les deux mots de passe doivent être identiques.'
      });
      return
    }
    if (password.length < 6) {
      $.notify.warning({
        title: 'Attention',
        text: 'Le mot de passe doit contenir au moins 6 caractères.'
      });
      return
    }
    API.post('/register', {
      pseudo: pseudo,
      password: password
    }, function () {
      switchCard();
      $('.card#login input#pseudo').val(pseudo).parent().addClass('is-dirty is-upgraded');
      $.notify.success({
        title: 'Success',
        text: 'Enregistrement'
      })
    })
  }
});
