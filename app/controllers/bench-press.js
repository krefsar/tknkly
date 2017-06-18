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
    const windowHeight = $(window).height() / 2;
    const leftAdjust = +this.get('leftHeight') + windowHeight;
    return Ember.String.htmlSafe(`top: ${leftAdjust}px`);
  }),

  rightStyle: computed('rightHeight', function() {
    const windowHeight = $(window).height() / 2;
    const rightAdjust = +this.get('rightHeight') + windowHeight;
    return Ember.String.htmlSafe(`top: ${rightAdjust}px`);
  }),

  lineX1: computed('leftHeight', function() {
    return '0';
  }),

  lineY1: computed('leftHeight', function() {
    return +this.get('leftHeight') + 32 + $(window).height() / 2;
  }),

  lineX2: computed('rightHeight', function() {
    return $(window).width();
  }),

  lineY2: computed('rightHeight', function() {
    return +this.get('rightHeight') + 32 + $(window).height() / 2;
  }),

  handleMessageLeft(event) {
    this.set('leftHeight', event.data);
  },

  handleMessageRight(event) {
    this.set('rightHeight', event.data);
  }
});
