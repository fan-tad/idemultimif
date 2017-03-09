/**
 * Created by guillaumemartinez on 17/10/2016.
 */
var QueryString = function () {
  // This function is anonymous, is executed immediately and
  // the return value is assigned to QueryString!
  var query_string = {};
  var query = window.location.search.substring(1);
  var vars = query.split('&');
  for (var i = 0; i < vars.length; i++) {
    var pair = vars[i].split('=');
    // If first entry with this name
    if (typeof query_string[pair[0]] === 'undefined') {
      query_string[pair[0]] = decodeURIComponent(pair[1]);
      // If second entry with this name
    } else if (typeof query_string[pair[0]] === 'string') {
      var arr = [query_string[pair[0]], decodeURIComponent(pair[1])];
      query_string[pair[0]] = arr;
      // If third or later entry with this name
    } else {
      query_string[pair[0]].push(decodeURIComponent(pair[1]))
    }
  }
  return query_string
}();
$(function () {
  /**
   * handler add new user
   */
  $('.modal#config-project .add-user').on('click', function () {
    var username = prompt("Nom de l'utilisateur", "");
    if (username !== '' && username !== null) {
      $('.modal#config-project .users').append(
        $('<li>').addClass('mdl-list__item').empty().append(
          $('<label>').addClass('username').attr('for', username).text(username),
          $('<div>').addClass('mdl-selectfield mdl-js-selectfield mdl-selectfield--floating-label').attr('id', username).addClass('is-dirty is-upgraded').append(
            $('<select>').addClass('mdl-selectfield__select').attr('id', 'role').append(
              $('<option>').attr('value', 'reporter').attr('selected', true).text('Reporter'),
              $('<option>').attr('value', 'dev').text('Dev'),
              $('<option>').attr('value', 'admin').text('Administrateur')
            ),
            $('<label>').addClass('mdl-selectfield__label').attr('for', 'role').text('Role')
          )
        )
      )
    }
  })
  /**
   * Open ticket dialog
   */
  $('.parameters .list #project-config').on('click', function (event) {
    event.preventDefault();
    event.stopPropagation();
    $('.modal#config-project').modal()
  });
  /**
   * Validate ticket dialog
   */
  $('.modal#config-project .save').on('click', postSaveConfig);
  /**
   * Load all iframe
   */
  function loadContent () {
    $('.iframe #code .page-content').load('iframe/code.html');
    $('.iframe #ide .page-content').load('iframe/ide.html');
    $('.iframe #ticket .page-content').load('iframe/ticket.html');
    $('.iframe #repo .page-content').load('iframe/repo.html');
    $('.iframe #todo .page-content').load('iframe/todo.html');
    $('.iframe #forum .page-content').load('iframe/forum.html');
    $('.iframe #wiki .page-content').load('iframe/wiki.html');
    $('.iframe #doc .page-content').load('iframe/doc.html');
    $('body .chat').load('iframe/chat.html');
    hljs.initHighlightingOnLoad()
  }

  /**
   * Change project title on top
   * @param name
   */
  function setProjectName (name) {
    $('#project-title').text(name)
  }

  /**
   * Prepare config div
   * @param data
   */
  function prepareConfig (data) {
    $('.modal#config-project .project-name input').val(data.name).parent().addClass('is-dirty is-upgraded');
    $('.modal#config-project .users').empty()
    data.users.forEach(function (user) {
      $('.modal#config-project .users').append(
        $('<li>').addClass('mdl-list__item').empty().append(
          $('<label>').addClass('username').attr('for', user.name).text(user.name),
          $('<div>').addClass('mdl-selectfield mdl-js-selectfield mdl-selectfield--floating-label').attr('id', user.name).addClass('is-dirty is-upgraded').append(
            $('<select>').addClass('mdl-selectfield__select').attr('id', 'role').append(
              $('<option>').attr('value', 'reporter').attr('selected', user.role === 'reporter').text('Reporter'),
              $('<option>').attr('value', 'dev').attr('selected', user.role === 'dev').text('Dev'),
              $('<option>').attr('value', 'admin').attr('selected', user.role === 'admin').text('Administrateur')
            ),
            $('<label>').addClass('mdl-selectfield__label').attr('for', 'role').text('Role')
          )
        )
      )
    })
  }

  /**
   * Save the config
   */
  function postSaveConfig () {
    var projName = $('.modal#config-project .project-name input').val();
    var users = [];
    $('.modal#config-project .users li').each(function (index, value) {
      users.push({
        name: $(value).children('.username').text(),
        role: $(value).find('select').val()
      })
    });
    API.post('/project', {
      project: QueryString.project,
      name: projName,
      users: JSON.stringify(users)
    }, function () {
      $.notify.success({
        title: 'Success',
        text: 'Modification de la configuration du projet.'
      })
      window.location.search = `?project=${QueryString.project}`
    })
  }

  /**
   * Get project content
   */
  function getProject () {
    if (QueryString.project) {
      API.get('/project', {
        project: QueryString.project
      }, function (data) {
        prepareConfig(data);
        setProjectName(data.name)
      })
    } else {
      window.location = 'projects.html'
    }
  }

  getProject();
  loadContent()
});
