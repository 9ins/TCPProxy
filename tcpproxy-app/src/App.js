import React, {Component} from 'react'
import { render } from 'react-dom';
import ReactDOM from 'react-dom';
import LogIn from './LogIn';

class App extends Component {

  constructor(props) {
    super(props);
    console.log("constructor: start of class");
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

  render() {
    return (
      <div className="App">
          <LogIn></LogIn>
      </div>
    );
  }
}


export default App;


