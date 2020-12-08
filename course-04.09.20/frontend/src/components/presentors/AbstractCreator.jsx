import React from "react"

class AbstractCreator extends React.Component {

    onTrigger = (formData) => { this.props.parentCallback(formData) }
}

export default AbstractCreator