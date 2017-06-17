import Ember from 'ember';

const {
  $,
  Controller,
  computed
} = Ember;

export default Controller.extend({
  leftHeight: 0,
  rightHeight: 0,

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

  leftStyle: computed('leftHeight', function() {
    return Ember.String.htmlSafe(`top: ${this.get('leftHeight')}px`);
  }),

  rightStyle: computed('rightHeight', function() {
    return Ember.String.htmlSafe(`top: ${this.get('rightHeight')}px`);
  }),

  lineX1: computed('leftHeight', function() {
    return '0';
  }),

  lineY1: computed('leftHeight', function() {
    return +this.get('leftHeight') + 32;
  }),

  lineX2: computed('rightHeight', function() {
    return $(window).width();
  }),

  lineY2: computed('rightHeight', function() {
    return +this.get('rightHeight') + 32;
  }),

  handleMessageLeft(event) {
    this.set('leftHeight', event.data);
  },

  handleMessageRight(event) {
    this.set('rightHeight', event.data);
  }
});
