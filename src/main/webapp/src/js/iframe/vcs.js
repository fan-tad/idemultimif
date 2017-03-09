/**
 * Created by guillaumemartinez on 23/11/2016.
 */
$(function () {
  /**
   * click on commit button
   */
  $('#ide .actions .commit').on('click', function () {
    var commitMessage = prompt('Message du commit', '')
    if (commitMessage !== "" && commitMessage !== null) {
      postCommit(commitMessage)
    } else {
      $.notify.warning({
        title: 'Attention',
        text: 'Aucun message de commit fourni.'
      });
    }
  })
  /**
   * click on push button
   */
  $('#ide .actions .push').on('click', function () {
    if (confirm('Voulez vous Push ?')) {
      postPush()
    }
  })
  /**
   * click on pull button
   */
  $('#ide .actions .pull').on('click', function () {
    if (confirm('Voulez vous Pull ?')) {
      postPull()
    }
  })
  /**
   * click on create branch button
   */
  $('#ide .actions .create-branch').on('click', function () {
    var branchName = prompt('Nom de la nouvell branche', '')
    if (branchName) {
      postNewBranch(branchName, function () {
        $.notify.success({
          title: 'Success',
          text: 'Branche crée avec succès.'
        })
        getBranches(function (data) {
          appendBranches(data.current, data.all)
        })
      })
    } else {
      $.notify.warning({
        title: 'Attention',
        text: 'Aucun nom fourni pour la nouvelle branche.'
      });
    }
  })
  /**
   * click on compile button
   */
  $('#ide .actions .compile').on('click', function () {
    $('.modal#compile-console').modal()
  })
  /**
   * click on compile button
   */
  $('#ide .actions .doc').on('click', function () {
    postGenerateJavaDoc(function(){
      $.notify.success({
        title: 'Success',
        text: 'La doc est en cours de génération.'
      })
    })
  })
  /**
   * On change compile lang
   */
  $('.modal#compile-console .lang').on('change', function () {
    $('.modal#compile-console .commentaire > *').hide()
    $(`.modal#compile-console .commentaire .${$(this).val()}`).show()
  })
  /**
   * On click start compile
   */
  $('.modal#compile-console .play').on('click', function () {
    var lang = $('.modal#compile-console .lang').val()
    postCompile(lang, function () {
      $('.modal#compile-console .mdl-progress').css('display', 'inline-block')
      var $stdout = $('.modal#compile-console .stdout')
      var $stderr = $('.modal#compile-console .stderr')
      var timeOut = setInterval(function () {
        getFileContent('out/stdout.txt', function (data) {
          $stdout.text(data)
          hljs.highlightBlock($stdout.get(0));
        })
        setTimeout(function () {
          getFileContent('out/stderr.txt', function (data) {
            $stderr.text(data)
            hljs.highlightBlock($stderr.get(0));
          })
        }, 2000)
      }, 5000)
      $('.modal#compile-console .ferme, .modaler').on('click', function () {
        clearTimeout(timeOut)
        $('.modal#compile-console .mdl-progress').css('display', 'none')
      })
    })
  })
  /**
   * generate branche select
   * @param current
   * @param branches
   */
  function appendBranches (current, branches) {
    var $select = $('#ide .actions .branch-list')
    $select.empty()
    $select.on('change', function () {
      postCheckout($(this).val(), function () {
        window.location.reload()
      })
    })
    for (var b in branches) {
      var branch = branches[b].split('/').pop();
      $select.append(
        $('<option>').attr('id', branch).attr('value', branch).attr('selected', branch == current).text(branch)
      )
    }
  }

  /**
   * Commit
   * @param message
   */
  function postCommit (message) {
    API.post('/git/commit', {
      project: QueryString.project,
      message: message
    }, function () {
      $.notify.success({
        title: 'Success',
        text: 'Commit avec succès.'
      })
    })
  }

  /**
   * Push
   */
  function postPush () {
    API.post('/git/push', {
      project: QueryString.project
    }, function () {
      $.notify.success({
        title: 'Success',
        text: 'Push avec succès.'
      })
    })
  }

  /**
   * Pull
   */
  function postPull () {
    API.post('/git/pull', {
      project: QueryString.project
    }, function () {
      $.notify.success({
        title: 'Success',
        text: 'Pull avec succès.'
      })
      window.location.reload()
    })
  }

  /**
   * Create new branch
   * @param branch
   * @param onSuccess
   */
  function postNewBranch (branch, onSuccess) {
    API.post('/git/branch/create', {
      project: QueryString.project,
      branch: branch
    }, onSuccess)
  }

  /**
   * Checkout
   * @param branch
   * @param onSuccess
   */
  function postCheckout (branch, onSuccess) {
    API.post('/git/branch/checkout', {
      project: QueryString.project,
      branch: branch
    }, onSuccess)
  }

  /**
   * get all branches
   * @param onSuccess
   */
  function getBranches (onSuccess) {
    API.get('/git/branches', {
      project: QueryString.project
    }, onSuccess)
  }

  /**
   * compile
   * @param lang
   * @param onSuccess
   */
  function postCompile (lang, onSuccess) {
    API.post('/git/compile', {
      project: QueryString.project,
      lang: lang,
    }, onSuccess)
  }

  /**
   * get file content
   * @param file
   * @param onSuccess
   */
  function getFileContent (file, onSuccess) {
    API.get('/file', {
      project: QueryString.project,
      path: file
    }, onSuccess)
  }

  function postGenerateJavaDoc(onSuccess){
    API.post('/doc/create', {
      project: QueryString.project,
    }, onSuccess)
  }
  /**
   * default call on load
   */
  getBranches(function (data) {
    appendBranches(data.current, data.all)
  })
})