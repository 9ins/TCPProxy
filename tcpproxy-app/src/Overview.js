import { Panel, Tabs, useTabState, usePanelState } from "@bumaga/tabs";
import React, {Component} from 'react'

class Overview extends Component {

    render() {
        return (
            <div>
            {this.props.title}
            <br></br>        
            <button onClick=''>click</button>
            </div>
        )
    }

}

export default Overview;