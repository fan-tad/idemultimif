/**
 * Created by guillaumemartinez on 17/10/2016.
 */
$(function () {
  /**
   * Append a t0d0 to the ist of t0d0
   * @param file
   * @param comment
   * @param code
   */
  function addTodo (id, file, code) {
    $('.todo-list .list').append(
      $('<div>').addClass('todo mdl-list__item').attr('id', id).append(
        $('<span>').addClass('content mdl-list__item-primary-content').append(
          $('<span>').addClass('file').text(file),
          $('<pre>').append($('<code>').addClass('comment hljs').html(hljs.highlightAuto(code).value))
        )
      ).on('click', function () {
        $('.modal#show-todo pre code').html(hljs.highlightAuto(code).value);
        $('.modal#show-todo').modal()
      })
    )
  }

  /**
   * Get the list of t0d0
   */
  function getTodo () {
    API.get('/todo', {
      project: QueryString.project
    }, function (data) {
      data.forEach(function (d) {
        addTodo(d.id, d.name, d.description)
      })
    })
  }

  /**
   * Default call on load
   */
  getTodo()
});
