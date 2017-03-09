/**
 * Created by guillaumemartinez on 17/10/2016.
 */
$(function () {
  /**
   * Event
   * Return to table of files
   */
  $('.file-overview .back').on('click', function () {
    showTable()
  });
  /**
   * Clear table of files
   */
  function clearTable () {
    $('table.code tbody').empty()
  }

  /**
   * Add file to the table
   * @param type - Type file (file / dir)
   * @param name - File name
   * @param commit - Commit message
   * @param date - Commit date
   */
  function addFile (type, name, commit, date, path) {
    if (name == null) {
      return
    }
    if (type !== 'dir' && type !== 'file') {
      type = 'file'
    }
    $('table.code tbody').append(
      $('<tr>').append(
        $('<td>').append(
          $('<a>').attr('href', '#').append(
            $('<i>').addClass(type === 'dir' ? 'fa fa-folder': 'fa fa-file'),
            $('<span>').text(name)
          ).on('click', type === 'dir' ? function () {
            getFolder(`${path}/${name}/`, function (data) {
              clearTable();
              addFile('dir', path.split('/').pop(), null, '-', `${path}/`)
              for (var f in data) {
                var file = data[f]
                addFile(file.type, file.name, file.commit, file.date, `${path}/${name}/`)
              }
            })
          }: function () {
            getFileContent(`${path}/${name}/`, function (data) {
              showCode();
              appendFile(name, name.split('.')[-1], data)
            })
          })
        ),
        $('<td>').text(commit),
        $('<td>').text((new Date(date)).toLocaleString())
      )
    )
  }

  /**
   * Show code of a file
   */
  function showCode () {
    $('#code .page-content .file-overview').show();
    $('#code .page-content table').hide()
  }

  /**
   * Show table of files
   */
  function showTable () {
    $('#code .page-content .file-overview').hide();
    $('#code .page-content table').show()
  }

  /**
   * Append the content file to the code review
   * @param name
   * @param lang
   * @param code
   */
  function appendFile (name, lang, code) {
    $('#code .page-content .file-overview .file-name').text(name);
    $('#code .page-content .file-overview pre').html(
      $('<code>').attr('class', lang).text(code)
    );
    // A verif
    hljs.highlightBlock($('#code .page-content .file-overview pre code').get(0))
  }

  /**
   * Get list of file inside this folder
   * @param folder
   * @param onSuccess
   */
  function getFolder (folder, onSuccess) {
    API.get('/folder', {
      project: QueryString.project,
      path: folder
    }, onSuccess)
  }

  /**
   * Get file content
   * @param file
   * @param onSuccess
   */
  function getFileContent (file, onSuccess) {
    API.get('/file', {
      project: QueryString.project,
      path: file
    }, onSuccess)
  }

  /**
   * On load default call
   */
  getFolder('/', function (data) {
    clearTable();
    for (var f in data) {
      var file = data[f]
      addFile(file.type, file.name, file.commit, file.date, "")
    }
  })
});
