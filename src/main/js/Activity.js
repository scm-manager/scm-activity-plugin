//@flow
import React from "react";
import { translate } from "react-i18next";
import {withRouter} from "react-router-dom";
import {Loading , ErrorNotification} from "@scm-manager/ui-components";

type Props = {
  activityUrl: any,
  // context prop
  t: string => string
};

type State = {
  loading: boolean,
  success?: boolean,
  error?: string
};

class Activity extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);
    this.state = { loading: false };
  }

  render() {
    const { t , activityUrl} = this.props;
    const { loading, success, error } = this.state;

    if (loading) {
      return <Loading />;
    }

    if (error) {
      return  <ErrorNotification error={error.message} />;
    }

    return (
        <div>Hello Activity  {activityUrl}</div>
    );
  }
}


export default withRouter(translate("plugins")(Activity));

