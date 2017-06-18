import Ember from 'ember';

export default Ember.Controller.extend({
  doneConnecting: false,

  init() {
    this._super();
    let socket = this.get('websockets').socketFor('ws://localhost:7000');
    socket.on('open', (event) => {
      console.log('Connection established.');
      this.send('connected');
    });
  },

  actions: {
    connected() {
      this.set('doneConnecting', true);
      Ember.run.later(this, () => {
        this.send('startBicepCurl');
      }, 2000);
    }
  }
});
