/**
 * Created by guillaumemartinez on 14/10/2016.
 */
$(function () {
  /**
   * Open project dialog
   */
  $('.project-list button.add').on('click', function () {
    $('.modal#create-project').modal()
  });
  /**
   * Validate project dialog
   */
  $('.modal#create-project .create').on('click', postNewProject);
  /**
   * Add a project to the list
   * @param icon
   * @param name
   * @param description
   */
  function addProject (icon, name, id, description) {
    $('.project-list .list').append(
      $('<div>').addClass('project mdl-list__item').attr('id', id).append(
        $('<span>').addClass('content mdl-list__item-primary-content').append(
          $('<div>').addClass('img').append(
            $('<img>').attr('src', icon)
          ),
          $('<span>').addClass('title').text(name),
          $('<span>').addClass('description').text(`${description.slice(0, 320)}`)
        ),
        $('<a>').addClass('mdl-list__item-secondary-action').attr('href', '#').append(
          $('<i>').addClass('fa fa-trash').attr('id', id).on('click', function (event) {
            event.stopPropagation();
            postDeleteProject(id)
          })
        )
      ).on('click', function () {
        window.location = `project.html?project=${id}`
      })
    );
    componentHandler.upgradeDom()
  }

  /**
   * Create new project
   */
  function postNewProject () {
    var nom = $('.modal#create-project input#nom').val();
    var description = $('.modal#create-project textarea#description').val();
    if (nom === '' || description === '') {
      $.notify.warning({
        title: 'Attention',
        text: 'Tous les champs doivent être rempli.'
      });
      return
    }
    nom = nom.replace(/[^a-zA-Z0-9-_]/g, '_');
    API.post('/projects', {
      action: 'create',
      nom: nom,
      description: description
    }, function () {
      $.notify.success({
        title: 'Success',
        text: 'Creation du projet avec succes.'
      });
      getProjects(appendProjects)
    })
  }

  /**
   * Delete project
   * @param nom
   */
  function postDeleteProject (id) {
    API.post('/projects', {
      action: 'delete',
      id: id
    }, function () {
      $.notify.success({
        title: 'Success',
        text: 'Suppression du projet avec succes.'
      });
      $(`.project-list .list .project#${id}`).remove()
    })
  }

  /**
   * Add project to the list
   * @param data
   */
  function appendProjects (data) {
    $('.project-list .list *').remove()
    data.forEach(function (d) {
      addProject('src/image/sharkode.png', d.name, d.id, d.description)
    })
  }

  /**
   * Get the list of projects
   */
  function getProjects (onSuccess) {
    API.get('/projects', {}, onSuccess)
  }

  getProjects(appendProjects)
});
