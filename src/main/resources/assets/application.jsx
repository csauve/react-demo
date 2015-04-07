console.log("Hello!");

var loadSchema = function() {
  $.ajax({
    url: "/api/account/1",
    type: "options",
    success: function(data, status) {
      console.dir(data);
      React.render(<DynamicForm schema={data}/>, document.getElementById("app-container"));
    }
  });
};

var DynamicForm = React.createClass({
  render: function() {
    return <StringField value="test"/>;
  }
});

var StringField = React.createClass({
  render: function() {
    return <input type="text" value={this.props.value} />;
  }
});

