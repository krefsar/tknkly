import Ember from 'ember';

export default Ember.Route.extend({
  actions: {
    startBenchPress() {
      console.log('transitioning to bench press');
      this.transitionTo('bench-press');
    }
  }
});
