export default function() {
  this.transition(
    this.fromRoute('index'),
    this.toRoute('calibration'),
    this.use('toLeft'),
    this.reverse('toRight')
  );

  this.transition(
    this.fromRoute('calibration'),
    this.toRoute('bench-press'),
    this.use('toLeft'),
    this.reverse('toRight')
  );

  this.transition(
    this.hasClass('animation-transition'),
    this.use('scale', { duration: 500 })
  );
};
