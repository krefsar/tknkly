import Ember from 'ember';

export default Ember.Controller.extend({
  satoriManager: Ember.inject.service(),
  satoriErrors: Ember.inject.service(),
  doneConnecting: false,

  init() {
    this._super();
    this.get('satoriManager').initializeSatori();
    let rtm = this.get('satoriManager').get('satoriRtm');
    rtm.on("enter-connected", () => {
        this.send('connected');
    });
    this.get('satoriErrors').initializeSatori();
    let rtmErrors = this.get('satoriErrors').get('satoriRtm');
    rtmErrors.on('enter-connected', () => {
      this.send('errorsConnected');
    });
    this.get('satoriManager').startSatori();
    this.get('satoriErrors').startSatori();
  },

  finalizeConnections() {
    this.set('doneConnecting', true);
    Ember.run.later(this, () => {
      this.send('startBicepCurl');
    }, 2000);
  },

  actions: {
    connected() {
      this.set('connected', true);
      if (this.get('errorsConnected')) {
        this.finalizeConnections();
      }
    },

    errorsConnected() {
      this.set('errorsConnected', true);
      if (this.get('connected')) {
        this.finalizeConnections();
      }
    }
  }
});
