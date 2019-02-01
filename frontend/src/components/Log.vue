<template>
    <div class="bootstrap">
        <b-card style="max-width: 400px">
            <b-alert slot="header"  :variant="connected==false ? 'danger' : 'success'" show>

                <div class="form-group">

                    <b-form-select v-model="selected" :options="partner_systems" class="mb-3" />
                    <label for="connect">Connection status:
                        <icon name="signal" :hidden="connected == false" />
                        <icon name="exclamation-circle" :hidden="connected == true" />
                    </label>
                    <b-button id="connect" variant="outline-success" size="sm" type="submit" :hidden="connected == true" :disabled="selected == null||connected == true" @click.prevent="connect">Connect</b-button>
                    <b-button id="disconnect"  variant="outline-danger" size="sm" type="submit" :hidden="connected == false" :disabled="connected == false" @click.prevent="disconnect">Disconnect</b-button>
                </div>
            </b-alert>
            <b-input-group>

                <b-button @click="update()" variant="outline-primary" :disabled="connected == false || selected == null">
                    <icon name="sync"/> RELOAD FILE
                </b-button>
                <b-button @click="fetch()" variant="outline-success" :disabled="connected == false || selected == null">
                    <icon label="No Photos">
                        <icon scale="0.5" name="arrow-circle-up"></icon>
                        <icon name="redo" scale="1" class="alert"></icon>
                    </icon>
                    Fetch
                </b-button>
                <b-form-input v-model="increment_by" type="number" min="5"></b-form-input>
            </b-input-group>

        </b-card>

        <div>
        </div>
        <p class="log" v-for="item in r_messages">
            {{item}}
        </p>
    </div>
</template>


<script>
    import SockJS from "sockjs-client";
    import Stomp from "webstomp-client";
    import {AXIOS} from './http-common'


    export default {
        data() {
            return {
                selected: null,
                partner_systems:[],
                received_messages: [],
                increment_by:5,
                from: 0,
                to: 0,
                connected: false
            }
        },
        computed: {
            r_messages: function () {
                return this.received_messages.reverse();
            }
        },
        methods: {
            fetch() {
                console.log("Request lines from " + this.from + " to " + this.to);
                if (this.stompClient && this.stompClient.connected) {
                    if(this.from == 0)
                        this.from = 1
                    this.to = parseInt(this.to) + parseInt(this.increment_by);
                    let msg = {
                        "from": this.from,
                        "to": this.to
                    };
                    this.stompClient.send("/socket-api/log/"+this.selected, JSON.stringify(msg), {});
                }
            },
            update(){
                this.received_messages = [];
                this.from = 0;
                this.to = 5;
                let msg = {
                    "from": this.from,
                    "to": this.to
                };
                if (this.stompClient && this.stompClient.connected) {
                    this.stompClient.send("/socket-api/log/update/"+this.selected, JSON.stringify(msg), {});
                }
            },
            connect() {
                if(this.selected != null){
                    this.socket = new SockJS(process.env.VUE_APP_API_ENDPOINT+"/websocket-server");
                    this.stompClient = Stomp.over(this.socket);
                    this.stompClient.connect(
                        {},
                        frame => {
                            this.connected = true;
                            console.log(frame);
                            this.stompClient.subscribe("/broadcast/log/"+this.selected, tick => {
                                console.log(tick);
                                this.received_messages.push(tick.body);
                                this.from = parseInt(this.to) + 1
                            });
                        },
                        error => {
                            console.log(error);
                            this.connected = false;
                        }
                    );
                }
            },
            disconnect() {
                if (this.stompClient) {
                    this.stompClient.disconnect();
                }
                this.connected = false;
            },
            tickleConnection() {
                this.connected ? this.disconnect() : this.connect();
            },
            fetchPartners: function(){
                AXIOS.get(`/partners`)
                    .then(response => {
                        // JSON responses are automatically parsed.
                        let partners = response.data.map(partner=>({"value":partner.id, "text":partner.name}))
                        this.partner_systems = partners
                        console.log(response.data)
                    })
                    .catch(e => {
                        this.errors.push(e)
                    })
            }
        },
        mounted: function(){
            this.fetchPartners();
            console.log("On mounted");
        }
    };

</script>

<style>
    body{
        padding: 1em;
    }
    p.log {
        margin: 1em;
        padding-right: 1em;
    }
</style>
