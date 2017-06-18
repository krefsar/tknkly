import Ember from 'ember';

const {
  Controller,
  computed,
  String
} = Ember;

export default Controller.extend({
  currentAngle: -90,
  maxAngle: 90,
  minAngle: -90,

  computedAngle: computed('currentAngle', 'maxAngle', 'minAngle', function() {
    let value = parseInt(this.get('currentAngle'), 10) * -1;
    if (value < this.get('minAngle')) {
      value = this.get('minAngle');
    }

    if (value > this.get('maxAngle')) {
      value = this.get('maxAngle');
    }

    return String.htmlSafe(`transform: rotate(${value}deg)`);
  }),

  init() {
    this._super();
    let socket = this.get('websockets').socketFor('ws://localhost:7000');
    socket.on('message', (event) => {
      this.handleMessage(event);
    });
  },

  handleMessage(event) {
    this.set('currentAngle', event.data);
  }
});
