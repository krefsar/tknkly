import Ember from 'ember';

export default Ember.Controller.extend({
  doneConnecting: false,

  init() {
    this._super();
    let socketLeft = this.get('websockets').socketFor('ws://localhost:7000');
    let socketRight = this.get('websockets').socketFor('ws://localhost:7001');
    socketLeft.on('open', (event) => {
      console.log('Connection to left established.');
      this.send('leftConnected');
    });
    socketRight.on('open', (event) => {
      console.log('Connection to right established.');
      this.send('rightConnected');
    });
  },

  finalizeConnection() {
    this.set('doneConnecting', true);
    Ember.run.later(this, () => {
      this.send('startBenchPress');
    }, 1500);
  },

  actions: {
    leftConnected() {
      this.set('leftConnected', true);

      if (this.get('rightConnected')) {
        this.finalizeConnection();
      }
    },

    rightConnected() {
      this.set('rightConnected', true);
      if (this.get('leftConnected')) {
        this.finalizeConnection();
      }
    }
  }
});
