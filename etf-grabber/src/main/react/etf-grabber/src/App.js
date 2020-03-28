import React, {Component} from 'react';
import Button from "@material-ui/core/Button";
import Card from "@material-ui/core/Card";
import CardContent from "@material-ui/core/CardContent";
import Typography from "@material-ui/core/Typography";
import axios from 'axios';

class App extends Component {
    state = {
        map: {}
    };

    componentDidMount() {
        setInterval(() => {
            axios.get('/api/load/state')
                .then(res => {
                    const state = res.data;
                    this.setState({map: state})
                })
        }, 1000);
    }

    handleClick() {
        axios.post('/api/load/all')
            .catch(reason => {
                console.log(reason);
                alert(reason.data.message)
            });
    }

    render() {
        const items = Object.keys(this.state.map).map(key => {
            return (
                <Typography variant="h7" component="h4" key={key}>
                    <span style={{float: "left", width: "70px"}}>{key}</span>

                    <span style={{float: "left", width: "68px"}}
                          className="MuiTypography-colorTextSecondary">etf_info:</span>
                    <span style={{
                        float: "left",
                        width: "70px",
                        color: `${getColor(this.state.map[key].ETF_INFO)}`
                    }}>{this.state.map[key].ETF_INFO}</span>

                    <span style={{float: "left", width: "58px"}}
                          className="MuiTypography-colorTextSecondary">prices:</span>
                    <span style={{
                        float: "left",
                        width: "70px",
                        color: `${getColor(this.state.map[key].PRICES)}`
                    }}>{this.state.map[key].PRICES}</span>

                    <span style={{float: "left", width: "85px"}}
                          className="MuiTypography-colorTextSecondary">dividends:</span>
                    <span
                        style={{color: `${getColor(this.state.map[key].DIVIDENDS)}`}}>{this.state.map[key].DIVIDENDS}</span>
                </Typography>
            )
        });
        return (
            <div>
                <div style={{textAlign: "center", marginTop: "50px"}}>
                    <Button variant="outlined" color="primary" onClick={this.handleClick}>Load all</Button>
                </div>
                <Card style={{width: "550px", margin: "50px auto"}}>
                    <CardContent>
                        {items}
                    </CardContent>
                </Card>
            </div>
        );
    }
}

function getColor(status) {
    if (status === 'OK') {
        return '#5eba7d';
    } else if (status === 'ERROR') {
        return '#e86441';
    } else {
        return '#0000008a'
    }
}

export default App;
