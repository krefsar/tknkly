import Ember from 'ember';

export default Ember.Controller.extend({
  leftHeight: 0,

  init() {
    this._super();
    let socketLeft = this.get('websockets').socketFor('ws://localhost:7000');
    let socketRight = this.get('websockets').socketFor('ws://localhost:7001');
    socketLeft.on('message', (event) => {
      this.handleMessageLeft(event);
    });
    socketRight.on('message', (event) => {
      this.handleMessageRight(event);
    });
  },

  handleMessageLeft(event) {
    this.set('leftHeight', event.data);
  },

  handleMessageRight(event) {
    this.set('rightHeight', event.data);
  }
});
