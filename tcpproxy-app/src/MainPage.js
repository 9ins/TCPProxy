import React from "react";
import { Tabs, useTabState, usePanelState } from "@bumaga/tabs";
import Overview from './Overview';
import Sessions from './Sessions';
import Analysis from './Analysis';
import Settings from './Settings';

const Tab = ({ children }) => {
  const { onClick } = useTabState();

  return <button onClick={onClick}>{children}</button>;
};

const Panel = ({ children }) => {
  const isActive = usePanelState();

  return isActive ? <p>{children}</p> : null;
};

export default () => (
  <Tabs>
    <div>
      <Tab>Overview</Tab>
      <Tab>Sessions</Tab>
      <Tab>Analysis</Tab>
      <Tab>Settings</Tab>
    </div> 

    <Panel>
        <div className="Overview">
        <Overview title="Overview Page"/>
        </div>
    </Panel >
    <Panel className="Sessions"></Panel >
    <Panel className="Analysis"></Panel >
    <Panel className="Settings"></Panel >
  </Tabs>
);