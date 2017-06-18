import Ember from 'ember';

const {
  Controller,
  computed
} = Ember;

export default Controller.extend({
  maxPitch: 90,
  minPitch: -90,
  minRoll: 0,
  maxRoll: 180,

  satoriManager: Ember.inject.service(),
  satoriErrors: Ember.inject.service(),

  computedRoll: computed('satoriManager.currentRoll', 'maxRoll', 'minRoll', function() {
    let satoriManager = this.get('satoriManager');
    let value = parseInt(satoriManager.get('currentRoll'), 10);
    if (value < this.get('minRoll')) {
      value = this.get('minRoll');
    }

    if (value > this.get('maxRoll')) {
      value = this.get('maxRoll');
    }

    return Ember.String.htmlSafe(`transform:rotate(${value * -1 + 90}deg)`);
  }),

  computedPitch: computed('satoriManager.currentPitch', 'maxPitch', 'minPitch', function() {
    let satoriManager = this.get('satoriManager');
    let value = parseInt(satoriManager.get('currentPitch'), 10);
    if (value < this.get('minPitch')) {
      value = this.get('minPitch');
    }

    if (value > this.get('maxPitch')) {
      value = this.get('maxPitch');
    }

    return Ember.String.htmlSafe(`transform: rotate(${value * -1}deg)`);
  }),

  computedYaw: computed('satoriManager.currentYaw', function() {
    let satoriManager = this.get('satoriManager');
    let value = parseInt(satoriManager.get('currentYaw'), 10);
    let center = parseInt(satoriManager.get('centerYaw'), 10);
    let delta = Math.abs(value - center + 90);
    let minYaw = 0;
    let maxYaw = 180;
    if (delta < minYaw) {
      delta = minYaw;
    }

    if (delta > 360) {
      delta -= 360;
    } else if (delta < -360) {
      delta += 360;
    }

    if (delta > maxYaw) {
      delta = maxYaw;
    }

    return Ember.String.htmlSafe(`transform: rotate(${+delta - 90}deg`);
  }),

  init() {
    this._super();
  }
});
