import Ember from 'ember';

export default Ember.Controller.extend({
  /*
  init() {
    this._super();
    let socket = this.get('websockets').socketFor('ws://localhost:7000/');
    socket.on('open', (event) => {
      this.openHandler(event);
    });
    socket.on('message', (event) => {
      this.messageHandler(event);
    });
    socket.on('close', (event) => {
      console.log('Socket closed.');
    });
  },

  messageHandler(event) {
    console.log(`got message: ${event.data}`);
    this.set('message', event.data);
  },

  openHandler(event) {
    console.log(`socket opened: ${event}`);
  },

  actions: {
    sendMessage() {
      let socket = this.get('websockets').socketFor('ws://localhost:7000');
      socket.send('a test message');
    }
  }
  */
});
