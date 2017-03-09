/**
 * Created by guillaumemartinez on 17/10/2016.
 */

$(function () {
  /**
   * FIx radio buttons switch
   */
  $('.modal#create-ticket span.mdl-radio__ripple-container').click(function () {
    $('.modal#create-ticket .mdl-radio').removeClass('is-checked')
    $(this).parent().addClass('is-checked')
  })
  /**
   * Return to the list of tickets
   */
  $('#ticket .show-ticket .back').on('click', hideTicket);
  /**
   * Open ticket dialog
   */
  $('#ticket .ticket-list button.add').on('click', function (event) {
    event.preventDefault();
    event.stopPropagation();
    $('.modal#create-ticket').modal()
  });
  /**
   * Validate ticket dialog
   */
  $('.modal#create-ticket .create').on('click', postNewTicket);
  /**
   * On click on chip change radio selection
   */
  $('.modal#create-ticket .mdl-chip').on('click', function () {
    $(this).parent().parent().find('.mdl-radio').trigger('click')
  });
  /**
   * Show specific ticket
   */
  function showTicket () {
    $('#ticket .ticket-list').hide();
    $('#ticket .show-ticket').show()
  }

  /**
   * Show list of tickets
   */
  function hideTicket () {
    $('#ticket .ticket-list').show();
    $('#ticket .show-ticket').hide()
  }

  /**
   * Clear all tickets
   */
  function clearTickets () {
    $('.ticket-list .list .ticket').remove()
  }

  /**
   * Append a ticket to the list
   * @param name
   * @param id
   * @param badge
   */
  function addTicket (name, id, badge) {
    $('.ticket-list .list').append(
      $('<div>').addClass('ticket mdl-list__item').attr('id', id).append(
        $('<span>').addClass('content mdl-list__item-primary-content').append(
          $('<span>').addClass('mdl-chip').attr('id', `${badge}-chip`).append(
            $('<span>').addClass('mdl-chip__text').text(badge)
          ),
          $('<span>').addClass('title').text(name)
        ),
        $('<a>').addClass('mdl-list__item-secondary-action').attr('href', '#').append(
          $('<i>').addClass('fa fa-trash').attr('id', id).on('click', function (event) {
            event.stopPropagation();
            postDeleteTicket(id)
          })
        )
      ).on('click', function () {
        getTicket(id, function (data) {
          appendTicket(data.name, data.user, data.badge, data.content);
          showTicket()
        })
      })
    );
    componentHandler.upgradeDom()
  }

  /**
   * Append the ticket content to the viewer
   * @param name
   * @param user
   * @param badge
   * @param content
   */
  function appendTicket (name, user, badge, content) {
    $('#ticket .show-ticket .title').text(name);
    $('#ticket .show-ticket .user').text(`${user}:`);
    $('#ticket .show-ticket .mdl-chip').attr('id', `${badge}-chip`);
    $('#ticket .show-ticket .mdl-chip .mdl-chip__text').text(badge);
    $('#ticket .show-ticket .content').text(content)
  }

  /**
   * Create a new ticket
   */
  function postNewTicket () {
    var nom = $('.modal#create-ticket input#nom').val();
    var description = $('.modal#create-ticket textarea#description').val();
    var badge = $('.modal#create-ticket .badges .is-checked input').val();
    if (nom === '' || description === '') {
      $.notify.warning({
        title: 'Attention',
        text: 'Tous les champs doivent être rempli.'
      });
      return
    }
    nom = nom.replace(/[^a-zA-Z0-9]/g, '_');
    API.post('/tickets', {
      project: QueryString.project,
      action: 'create',
      nom: nom,
      description: description,
      badge: badge
    }, function () {
      $.notify.success({
        title: 'Success',
        text: 'Creation du ticket avec succes.'
      });
      clearTickets();
      getTickets()
    })
  }

  /**
   * Get ticket content
   * @param id
   * @param cb
   */
  function getTicket (id, cb) {
    API.get('/ticket', {
      project: QueryString.project,
      id: id
    }, function (data) {
      cb(data)
    })
  }

  /**
   * Delete a ticket
   * @param nom
   */
  function postDeleteTicket (id) {
    API.post('/tickets', {
        project: QueryString.project,
        action: 'delete',
        id: id
      }, function () {
        $.notify.success({
          title: 'Success',
          text: 'Suppression du ticket avec succes.'
        });
        $(`.ticket-list .list .ticket#${id}`).remove()
      }
    )
  }

  /**
   * Get the list of tickets
   */
  function getTickets () {
    API.get('/tickets', {
      project: QueryString.project
    }, function (data) {
      data.forEach(function (d) {
        addTicket(d.name, d.id, d.badge)
      })
    })
  }

  /**
   * Default call on load
   */
  getTickets()
});
