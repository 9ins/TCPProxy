import React, {Component} from 'react'
import { render } from 'react-dom';
import ReactDOM from 'react-dom';

class AppExmaple extends Component {

  constructor(props) {
    super(props);
    console.log("constructor: start of class");
    this.state = {
      count: 0,
      hello: 'hello react!!!'
    };
  }

  componentDidMount() {
    console.log("End of first rendering of App");
  }

  componentDidUpdate(preProps, prevState) {
    console.log("Component is updated....");
    console.log(this.state, " values after updated");
    console.log(prevState, " values before updated");
  }

  componentWillUnmount() {
    console.log("Component will be unmounted........");
  }

  countUp = () => {
    this.setState({
      count: this.state.count + 1
    });
  }

  handleChanged = () => {
    this.setState({
      hello: "welcome to react world!!!"
    })
  };

  render() {
    return (
      <div className="AppExmaple">
        <h2><div>{this.props.message}</div></h2>

        <div>{this.state.count}</div>
        <div>{this.state.hello}</div> 
        <button onClick={this.handleChanged}>click me!</button>
        <button onClick={this.countUp}>count up!</button>
        <h3>App props</h3>
        <div className="inside-app-props">
          <InsideApp
            count = {this.state.count}
            handleChanged={this.countUp}
          />
        </div>
      </div>
    );
  }
}

class InsideApp extends Component {
  render() {
    return (
      <div>
        {this.props.count}
        <br></br>        
        <button onClick={this.props.handleChanged}>click!!!!</button>
      </div>
    )
  }
}

export default AppExmaple;


